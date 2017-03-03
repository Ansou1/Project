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

@interface ProfilViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIBarButtonItem *barButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *searchButton;

@property (weak, nonatomic) IBOutlet UIImageView *profileImageView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@property (strong, nonatomic) IBOutlet UILabel *abonnes;
@property (strong, nonatomic) IBOutlet UILabel *abonnements;

@property (strong, nonatomic) IBOutlet UIButton *star;

@property (weak, nonatomic) IBOutlet UILabel *pseudo;
@property (weak, nonatomic) IBOutlet UILabel *surname;
@property (weak, nonatomic) IBOutlet UILabel *name;
@property (weak, nonatomic) IBOutlet UILabel *email;
//@property (weak, nonatomic) IBOutlet UITextView *description;
@property (weak, nonatomic) IBOutlet UITextView *Message;

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
