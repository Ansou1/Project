//
//  ProfilViewController.h
//  MSW2
//
//  Created by simon on 15/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"


NSString *idProfil_global;

@interface ProfilViewController : UIViewController{
    NSString *urlPhoto;
    UIAlertController *alert_load;
}

@property (weak, nonatomic) IBOutlet UIBarButtonItem *barButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *searchButton;

@property (weak, nonatomic) IBOutlet UIImageView *profileImageView;

@property (strong, nonatomic) IBOutlet UILabel *abonnes;
@property (strong, nonatomic) IBOutlet UILabel *abonnements;

@property (strong, nonatomic) IBOutlet UIButton *star;

@property (strong, nonatomic) NSArray *ProfilModal;

@property(strong, nonatomic) NSString *idProfil;
@property(strong, nonatomic) NSString *isSuscribe;

@property (strong, nonatomic) NSMutableArray *pseudoCounter;
@property (strong, nonatomic) NSMutableArray *idRowCounter;
@property (strong, nonatomic) NSMutableArray *isSuscribeCounter;

@property (strong, nonatomic) NSMutableArray *pseudo2;
@property (strong, nonatomic) NSMutableArray *pseudo3;

- (IBAction)star:(id)sender;

@end
