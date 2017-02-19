//
//  MusicSheetViewController.h
//  MSW
//
//  Created by Antoine Simon on 19/02/2016.
//  Copyright © 2016 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"
#import "GuestMode.h"

@interface MusicSheetViewController : UIViewController <UIScrollViewDelegate>{
    NSString *UrlMusicSheet;
    NSString *Title;
    NSString *user;
    NSString *fid;
}

@property (strong, nonatomic) IBOutlet UIImageView *musicSheet;
@property (strong, nonatomic) IBOutlet UILabel *prompt;
@property (strong, nonatomic) IBOutlet UIButton *heart;
@property (strong, nonatomic) NSArray *musicSheetModal;
@property (strong, nonatomic) NSMutableArray *idRowCounter;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;

-(void)DisplayMusicSheet;
-(BOOL)checkSheetInTheList;
-(void)refreshStateButton;

- (IBAction)like:(id)sender;

@end
