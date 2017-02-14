//
//  MonCompteViewController.h
//  MSW2
//
//  Created by simon on 07/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"
#import "GuestMode.h"


@interface AccountViewController : UIViewController <UIActionSheetDelegate, UITextFieldDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, NSURLSessionTaskDelegate>  {

    //photo
    UIImagePickerController *picker;
    UIImagePickerController *picker2;
    UIImage *image;
    NSString *urlPhoto;
    
    UIAlertController *alert_load;

}

@property (weak, nonatomic) IBOutlet UIBarButtonItem *barButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *searchButton;
@property (weak, nonatomic) IBOutlet UIImageView *profileImageView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

-(IBAction)actionButton;

-(void) refresh;
-(void)TakePhoto;
-(void)ChooseExisting;
    

@end
